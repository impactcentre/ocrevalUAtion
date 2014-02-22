all:
	mvn clean package -DskipTests
	java -jar target/ocrevaluation.jar &
tar: 
	tar cvf ocrevaluation.tgz  .