# Challenge - Calculator with Kafka

This is a project that implements a distributed calculator application using Kafka as the messaging system. The application is divided into two modules:

- **rest**: Exposes the REST API to receive requests and send messages to Kafka.
- **calculator**: Consumes messages from Kafka, performs calculations, and can return the result or perform other processing.

## Requirements

Before getting started, ensure that you have the following dependencies installed:

- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/)
- [Kafka](https://kafka.apache.org/) (Kafka will be started via Docker Compose)

## Setup

This project contains two modules and uses Docker to orchestrate the execution. Kafka and Zookeeper will also be started automatically using Docker Compose.

### Project Structure

- **calculator**: Module responsible for consuming messages from Kafka and performing calculations.
- **rest**: Module responsible for exposing a REST API to interact with the system and send messages to Kafka.

### Running the Application

1. **Clone the repository**

   First, clone the repository:

   ```bash
   git clone https://github.com/larissavidal/calculator-app.git
   cd calculator-app

2. **Build the Docker images**
   The project uses Docker to run the containers. Run the following command to build the images:
    ````bash
   docker-compose build
   ````

3. **Start the containers**
   To start Kafka, Zookeeper, and both application modules (calculator and rest), run:
    ````bash
   docker-compose up
   ````

    This will start:
    - Zookeeper (required for Kafka)
    - Kafka (where messages will be sent and consumed)
    - calculator-service (which listens to the Kafka topic and performs calculations)
    - rest-service (which exposes the REST API to interact with the application)

4. **Accessing the REST API**
   After the containers are running, you can access the REST API at http://localhost:8080. The available route are:
   - GET /sum?a={value}&b={value}: Sends a message to Kafka with the values of a and b and requests the sum of the two numbers.
   - GET /sub?a={value}&b={value}: Sends a message to Kafka with the values of a and b and requests the substrate of the two numbers.
   - GET /multi?a={value}&b={value}: Sends a message to Kafka with the values of a and b and requests the multiplication of the two numbers.
   - GET /div?a={value}&b={value}: Sends a message to Kafka with the values of a and b and requests the divide of the two numbers.

5. **Checking messages in Kafka**
   The calculator module consumes messages sent to the calculator-topic Kafka topic. Each message contains the operation and numbers to be calculated.

6. **Configuration Parameters**
   If needed, you can adjust the following parameters in the docker-compose.yml file:
   - Kafka: Kafka broker configuration (by default, it runs on localhost:9092). 
   - Zookeeper: Required for Kafka operation (configured to run on localhost:2181).

7. **Message Structure**
   The application uses the OperationMessage class to represent the messages exchanged between modules. The structure is as follows:
    ````json
   {
      "correlationId": "unique-id",
      "operation": "sum",
      "a": 5,
      "b": 3
    }
   ````
8. **How It Works**
   1. The rest module receives an HTTP request (e.g., to sum two numbers) and sends a message to Kafka with the necessary data. 
   2. The calculator module listens to the calculator-topic Kafka topic, consumes the message, performs the calculation (e.g., sum or subtraction), and the result can be processed as needed. 
   3. Kafka ensures communication between the two modules, decoupling the business logic.

9. **Development**
   ***Modifying the Code***

   - Rest module: To modify the REST API routes, change the CalculatorController file in the rest module. 
   - Calculator module: To modify the calculation logic, change the CalculationService file in the calculator module.

10. **Testing**
    The project uses Kafka for message exchange, so tests can be done using a local environment with Kafka and Zookeeper running via Docker.

11. **Stopping the Containers**
    To stop the containers, run:
    ````bash
    docker-compose down
    ````

12. **Dependencies**
    - Kafka: Apache Kafka for message exchange. 
    - Spring Boot: Framework used for developing the rest and calculator modules. 
    - Docker: To facilitate running the application with containers.
