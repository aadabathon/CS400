runApplication:
	javac -cp .:../junit5.jar *.java
	java -cp . App

runAllTests:
	javac -cp .:../junit5.jar AppTestClass.java
	java -jar ../junit5.jar -cp . -c AppTestClass

clean:
	rm *.class
