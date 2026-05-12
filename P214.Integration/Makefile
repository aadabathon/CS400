PORT = 4242

startServer: compile
	java WebApp $(PORT)

runAllTests: compile
	java -jar ../junit5.jar -cp . -c BackendTests

clean:
	rm *.class

compile:
	javac -cp .:../junit5.jar *.java

