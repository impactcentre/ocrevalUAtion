#!/usr/bin/env ruby
require 'rubygems'
require 'grit'
require 'fast_xs'

$commits = {}

def find_children (head_commit, children, visited)
  commits = [head_commit]

  while not commits.empty? do
    if children.size > 0 and children.size % 1000 == 0 then
      puts "#processed child info for #{children.size} commits"
    end

    commit = commits.slice! 0
    if visited.has_key? commit.id then
      next
    end
    visited[commit.id] = true

    commit.parents.each do |c|
      if not children.has_key? c.id
        children[c.id] = []
      end

      if children[c.id].count{|com| com.id == commit.id} == 0
        children[c.id].push commit
      end

      #find_children(c, children)
      commits.push c
    end
  end
end

def plot_tree (head_commit, children, boring, plotted, decorations)
  to_plot = [[head_commit, []]]

  while not to_plot.empty? do
    nextdata = to_plot.slice! 0
    commit = nextdata[0]
    boring = nextdata[1]

    if plotted.has_key? commit.id
      next
    end

    if is_interesting(commit, children, decorations)
      make_node(commit, decorations)
      boring = []
    else
      boring += [commit]
    end

    parents_to_plot = []
    commit.parents.each do |c|
      if is_interesting(c, children, decorations)
        if is_interesting(commit, children, decorations)
          make_edge(commit, c)
        else
          puts "#boring commit #{commit.id}, interesting parent #{c.id}"
          make_elision(boring, c)
        end
        boring = [] #TODO not quite right ... need sub-boring list within inner loop
      else
        if is_interesting(commit, children, decorations)
          make_edge_to_elision(commit, c)
        end
      end

      parents_to_plot = [[c, boring]] + parents_to_plot
      #plot_tree(c, children, boring, plotted, decorations)
    end
    to_plot = parents_to_plot + to_plot

    if commit.parents.length == 0
      make_elision(boring, nil)
    end

    plotted[commit.id] = true
  end
end

def is_interesting(commit, children, decorations, neighbor=0)
  #merge or branch point
  if commit.parents.length > 1
    #puts "int true: #{commit.id} mult parents"
    return true
  end
  if children.include? commit.id and children[commit.id].length > 1
    #puts "int true: #{commit.id} mult children"
    #puts "int true: #{commit.id} child 1: #{children[commit.id][0]}"
    #puts "int true: #{commit.id} child 2: #{children[commit.id][2]}"
    return true
  end

  #decorated
  if decorations.has_key? commit.id
    #puts "int true: #{commit.id} decorated: #{decorations[commit.id]}"
    return true
  end

  if neighbor <= 0
    return false
  end

  #parent is interesting
  commit.parents.each do |p|
    if is_interesting(p, children, decorations, neighbor - 1)
      return true
    end
  end

  #child is interesting
  children[commit.id].each do |c|
    if is_interesting(c, children, decorations, neighbor - 1)
      return true
    end
  end

  return false
end

def nodes_for_interesting(commit, children, shown={})
  if shown.has_key? commit.id
    return
  end
  if is_interesting(commit, children)
    make_node(commit, {})
    shown[commit.id] = true
  end
  commit.parents.each do |p|
    nodes_for_interesting(p, children, shown)
  end
end

#def plot_tags(repo)
#  repo.tags.each do |tag|
#    puts "\"#{tag.name}\" -> \"#{tag.commit.id.slice 0,7}\";"
#    puts "\"#{tag.name}\" [shape=box, style=filled, color = yellow];"
#  end
#end
#      print "\""
#      print commit.message.gsub(%r|\n|, "\\n")
#      puts "\" -> \"#{commit.id.slice 0,7}\" [arrowhead=dot, color= lightgray, arrowtail=vee];"
#      print "\""
#      print commit.message.gsub(%r|\n|, "\\n")
#      puts "\" [shape=box, fontname=courier, fontsize = 8, color=lightgray, fontcolor=lightgray];"
#      puts "\"#{commit.id.slice 0,7}\" -> \"#{c.id.slice 0,7}\";"


def id_for(commit)
  commit.id.slice 0,6
end

def wrap_text(txt, col = 25)
  txt.gsub(/(.{1,#{col}})( +|$\n?)|(.{1,#{col}})/,
    "\\1\\3\n") 
end

def log_for(commit)
  commit_msg = commit.message.gsub(%r|git-svn-id: .*$|, "")
  cutoff = 297
  if commit_msg.length > cutoff+3
    commit_msg = commit_msg.slice(0,cutoff) + " ... (#{commit_msg.length-cutoff} longer)"
  end
  esc_log(wrap_text(commit_msg + " [" + id_for(commit) + "]")).gsub(%r|\n|, "<br/>")
end

def fixed(str)
  "<font face=\"Courier\">#{str}</font>"
end

def small(str)
  "<font point-size=\"9\">#{str}</font>"
end

def smaller(str)
  "<font point-size=\"8\">#{str}</font>"
end

def fmt_decor(d)
  case
    when d.is_a?(Grit::Tag) then color = "gold3"
    when d.is_a?(Grit::Head) then color = "forestgreen"
    else color = "orange3"
  end

  "<font color=\"#{color}\">#{d.name}</font>"
end

def color(commit)
  r = commit.id.slice(0,2).hex
  g = commit.id.slice(2,2).hex
  b = commit.id.slice(4,2).hex
  col = [r,g,b].collect{|c| [c, 192].min.to_s(16).rjust(2,"0")} * ""
  "color=\"##{col}\""
end

def make_node(commit, decorations, prefix="")
  label = smaller fixed log_for commit

  if decorations.has_key? commit.id
    label = decorations[commit.id].collect{|d| fmt_decor d} * "<br/>"
  end

  fill = ""
  if not decorations.has_key? commit.id
    fill = "style=filled fillcolor=gray75"
  end

  puts "\"#{prefix}#{commit.id}\" [label=<<font>#{label}</font>> #{fill}];"
end

def edge_weight(parent, child)
  1.0 - child.parents.index{|p| p.id == parent.id}.to_f / child.parents.length
end

def make_edge(c1, c2)
  puts "\"#{c2.id}\" -> \"#{c1.id}\" [weight=#{edge_weight(c2, c1)} #{color(c2)}];"
end

def make_edge_to_elision(commit, first_boring)
  puts "\"elide.#{first_boring.id}\" -> \"#{commit.id}\" [weight=#{edge_weight(first_boring,commit)} #{color(first_boring)}];"
end

def make_elision(boring_commits, interesting_commit)
  if boring_commits.length <= 0
    return
  end

  if boring_commits.length == 1
    make_node(boring_commits[0], {}, "elide.")
  else
    # since we're traversing backwards in time by following parent links,
    # the boring_commits list is in reverse chronological order
    # (see issue 2)
    rangeids = smaller fixed "#{id_for(boring_commits.last)}..#{id_for(boring_commits.first)}"
    rangedesc = small "#{boring_commits.size} commits"
    fill = "style=filled fillcolor=gray75"
    puts "\"elide.#{boring_commits.first.id}\" [label=<<font>#{rangedesc}<br/>#{rangeids}</font>> #{fill}];"
  end

  if not interesting_commit.nil?
    puts "\"#{interesting_commit.id}\" -> \"elide.#{boring_commits.first.id}\" [weight=#{edge_weight(interesting_commit,boring_commits.first)} #{color(interesting_commit)}];"
  end
end

if ARGV[1] == "--svg"
  #escape twice so that xml entities end up correct in svg files
  def esc_log(log)
    return log.fast_xs.fast_xs
  end
else
  def esc_log(log)
    return log.fast_xs
  end
end

#test command:
#for t in svg png; do
#  ./git-graph.rb test --${t} | dot -T${t} -otest.${t} /dev/stdin;
#done

if ARGV[0] == "test"
  puts "Digraph test { rankdir=LR;"
  class TestCommit
    def initialize(message, id)
      @message = message
      @id = id
    end
    attr_accessor :message
    attr_accessor :id
  end
  make_node(TestCommit.new("msg", "plain"), {})
  make_node(TestCommit.new("msg with entities: &nbsp; &amp; &lt; &gt; &quot;", "ent"), {})
  make_node(TestCommit.new("msg with \"quotes\"", "quot"), {})
  puts "}"
  exit()
end

repo = Grit::Repo.new(ARGV[0]);

decorated = (repo.branches + repo.remotes + repo.tags).collect{|r| r.commit}
decorated.reject!{|ref|
begin
  ref.parents
  false
rescue NoMethodError
  puts "#omitting #{ref} that doesn't know its parents"
  true
end}

puts "##{decorated.length} decorations"

children = {}
visited = {}
decorated.each do |c|
  puts "#finding children for #{c.id}"
  find_children(c, children, visited)
end

decorations = {}
repo.branches.each do |b|
  puts "#noting decoration info for branch #{b.name}"
  if not decorations.has_key? b.commit.id
    decorations[b.commit.id] = []
  end
  decorations[b.commit.id].push b
end
repo.remotes.each do |r|
  puts "#noting decoration info for remote branch #{r.name}"
  if not decorations.has_key? r.commit.id
    decorations[r.commit.id] = []
  end
  decorations[r.commit.id].push r
end
repo.tags.each do |t|
  puts "#noting decoration info for tag #{t.name}"
  if not decorations.has_key? t.commit.id
    decorations[t.commit.id] = []
  end
  decorations[t.commit.id].push t
end

puts "Digraph Git { rankdir=LR;"
plotted={}
decorated.each do |c|
  plot_tree(c, children, [], plotted, decorations)
  #nodes_for_interesting(c, children)
end
puts "}"
