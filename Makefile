clean-compile-test:
	mvn -DtestEnvironment=true clean compile test

test:
	mvn -DtestEnvironment=true test

docker-image:
	docker build -t delivery-microservice .