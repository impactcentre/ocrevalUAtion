all:
	mvn clean package -DskipTests
tar: 
	tar cvf ocrevaluation.tgz  .