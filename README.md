# stock-trading-with-java-isc1128
Summary: This sample Java code is for course ISC1128 - Stock Trading with Java. 
For access to this course, register and login at [learning.intersystems.com](https://learning.intersystems.com/) and 
then navigate to the course page: [Stock Trading with Java](https://learning.intersystems.com/course/view.php?name=Java%20Financial%20Play)

### IDE

We recommend you use Eclipse to run this sample code. You can also run this sample using other IDEs, such as IntelliJ or Netbeans.

### How to run

1.  Verify you have an [instance of InterSystems IRIS](https://learning.intersystems.com/course/view.php?name=Get%20InterSystems%20IRIS), and an IDE that supports Node.js (such as **Visual Studio Code**). If you are using AWS, Azure, or GCP, that you have followed the steps to [change the password for InterSystems IRIS](https://docs.intersystems.com/irislatest/csp/docbook/DocBook.UI.Page.cls?KEY=ACLOUD#ACLOUD_interact).

2.  If you are using AWS, GCP, or Microsoft Azure, load the sample stock data into InterSystems IRIS:  
    `$ iris load http://github.com/intersystems/Samples-Stock-Data`  
    If you are using InterSystems Labs, the sample stock data is already loaded. You can skip to the next step.

In your IDE:
1. Clone this repo.
2. Put the code files in a Java Project with JDK 1.7 or 1.8.
3. In the `config.txt`, modify the *ip* and *password* to be the correct values for your InterSystems IRIS instance. Although *port* and *username* are most likely the defaults, you should verify that the values are correct.
4. Execute the code.
  
Each task corresponds with a step in the course. If you simply would like to view the resulting sample code, view the last task for each section.

### Contents
* JDBC Sample code - Connect your Java application to InterSystems IRIS using JDBC to store and retrieve data with SQL.
* XEP Sample code - Connect your Java application to InterSystems IRIS to store real-time objects.
* Native API Sample code - Connect your Java application to InterSystems IRIS to store data natively and use methods built within InterSystems IRIS.
* Multi-model Sample code - Use JDBC, Native API, and XEP side-by-side to query data relationally, populate values using methods written within InterSystems IRIS, and store objects directly.
* Hibernate Sample code - Use the third-party tool, Hibernate, to do object relational mapping and interact with data in InterSystems IRIS.

Any and all code provided in these materials is provided solely for demonstrative and illustrative purposes and is not intended for use in production. 