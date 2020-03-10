FROM java:8
ADD target/comments-service.jar comments-service.jar
ENTRYPOINT ["java","-jar","comments-service.jar"]
