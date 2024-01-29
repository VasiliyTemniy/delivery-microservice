clean-compile-test:
	mvn -DtestEnvironment=true clean compile test

test:
	mvn -DtestEnvironment=true test