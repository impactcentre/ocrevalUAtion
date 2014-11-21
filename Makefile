all:
	mvn clean package -DskipTests
	java -jar target/ocrevaluation.jar &
compile:
	mvn clean package -DskipTests
	cp userProperties.xml target/

test:
	mvn clean package -DskipTests
#	java -cp target/ocrevaluation.jar eu.digitisation.Main -gt ~/x.txt -ocr ~/y.txt -ic -id -ip
	java -cp target/ocrevaluation.jar eu.digitisation.Main 

tar: 
	tar cvf ocrevaluation.tgz  .