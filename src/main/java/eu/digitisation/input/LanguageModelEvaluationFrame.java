package eu.digitisation.input;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import eu.digitisation.text.Text;
import java.awt.Font;
import javax.swing.JButton;

public class LanguageModelEvaluationFrame extends JFrame
{

    /**  */
    private static final long serialVersionUID = 4895806099667768081L;

    private JPanel contentPane;
    private JTextField thresholdTextField;
    private JSlider thresholdSlider;
    private JTextPane textPane;

    /** double array containing the perplexity values for every character. */
    private double[] perplexityArray;

    /** text style applied when the threshold value is exceeded. */
    private static final SimpleAttributeSet thresholdExceededStyle = new SimpleAttributeSet();
    /** default text style. */
    private static final SimpleAttributeSet defaultStyle = new SimpleAttributeSet();

    static
    {
        StyleConstants.setForeground(thresholdExceededStyle, Color.red);
        StyleConstants.setForeground(defaultStyle, Color.black);
    }

    /**
     * Create the frame.
     */
    public LanguageModelEvaluationFrame()
    {
        init();
    }

    /**
     * GUI initialization.
     */
    private void init()
    {
        setBounds(100, 100, 473, 347);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel panel = new JPanel();

        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE));
        gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
                gl_contentPane
                        .createSequentialGroup()
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)));

        textPane = new JTextPane();
        textPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
        scrollPane.setViewportView(textPane);

        thresholdTextField = new JTextField();
        thresholdTextField.setEditable(false);
        thresholdTextField.setFont(new Font("Tahoma", Font.BOLD, 16));
        thresholdTextField.setText("-1");
        thresholdTextField.setColumns(10);

        thresholdSlider = new JSlider();
        thresholdSlider.setValue(-1);
        thresholdSlider.setMaximum(-1);
        thresholdSlider.setMinimum(-50);
        thresholdSlider.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (!thresholdSlider.getValueIsAdjusting())
                {
                    thresholdTextField.setText((double) thresholdSlider.getValue() + "");
                    update();
                }
            }
        });
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
                    .addComponent(thresholdSlider, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(thresholdTextField, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(thresholdTextField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                        .addComponent(thresholdSlider, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                    .addContainerGap())
        );
        panel.setLayout(gl_panel);
        contentPane.setLayout(gl_contentPane);

    }

    /**
     * update the evaluation results.
     */
    private void update()
    {
        Double threshold = 0.0;
        try
        {
            threshold = Double.parseDouble(thresholdTextField.getText());
            StyledDocument document = textPane.getStyledDocument();

            document.setCharacterAttributes(0, document.getLength(), defaultStyle, true);
            for (int i = 0; i < perplexityArray.length; i++)
            {
                if (perplexityArray[i] < threshold)
                {
                    document.setCharacterAttributes(i, 1, thresholdExceededStyle, true);
                }
            }

        }
        catch (NumberFormatException nfe)
        {
            JOptionPane.showMessageDialog(this, "Unable to parse value '" + thresholdTextField.getText() + "'",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setInput(String textToEvaluate, double[] perplexityArray)
    {
        this.perplexityArray = perplexityArray;
        textPane.setText(textToEvaluate);
    }

    /**
     * Launch the application.
     */
    public static void main(final String[] args)
    {

        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    Text ocr = new Text(new File(args[0]));
                    final double[] perplexityArray =
                            new double[] {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0, 0.1, 0.2, 0.3, 0.4,
                                    0.5, 0.6, 0.7, 0.8, 0.9, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0,
                                    0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6,
                                    0.7, 0.8, 0.9, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0, 0.1, 0.2,
                                    0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8,
                                    0.9, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0, 0.1, 0.2, 0.3, 0.4,
                                    0.5, 0.6, 0.7, 0.8, 0.9, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0,
                                    0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9 };

                    LanguageModelEvaluationFrame frame = new LanguageModelEvaluationFrame();
                    frame.setInput(ocr.toString(), perplexityArray);
                    frame.setVisible(true);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
