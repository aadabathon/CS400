Run: AppMain.class
	@java AppMain

Test: TestApp.class
	@java -jar ../junit5.jar --class-path . --select-class TestApp

Jupiter:
	@echo "Hello Jupiter"

AppMain.class: AppMain.java
	@javac AppMain.java

TestApp.class: TestApp.java AppMain.class
	@javac -cp .:../junit5.jar TestApp.java

clean:
	rm *.class
