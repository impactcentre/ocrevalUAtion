all:
	mvn clean package -DskipTests
	java -jar target/ocrevaluation.jar &
compile:
	mvn clean package -DskipTests
	cp userProperties.xml target/

tar: 
	tar cvf ocrevaluation.tgz  .