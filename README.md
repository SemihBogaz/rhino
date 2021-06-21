## RHINO
Basic Android app for secure (end-to-end encrypted) messaging.
Made as a project for CSE 472 Information Systems Security.


# Contributors

- Semih Boğaz     20150807006
- Bekir Nazlıgül  20170808046

# Methodology

This project uses two encryption algorithms together for safety: RSA and AES. When sending a message a new AES key is generated and used to encrypt the message text. Then the receiver's public key is used in RSA to encrypt the AES key. This encrypted text and encrypted AES Key is delivered to the receiver, who then can decrypt and read it using their private RSA key.

As a result, the messages in delivery are always encrypted and is near impossible to crack and read. This picture shows what the server (Using FireBase storage) sees while two clients are messaging:

![Server's view of a message](https://github.com/SemihBogaz/rhino/blob/master/example.png)

Accounts for users are tied to their phone numbers, and requires two-factor authentication using Firebase Auth to create or access their messages:

![Two Factor Auth](https://github.com/SemihBogaz/rhino/blob/master/2fa.png)

A new AES key is generated for every message, ensuring high security. In the current version the RSA key pair is refreshed every time the application is restarted. This provides an additional layer of security.

The keys are kept in the application's internal storage in private mode, thus no other application can read or modify them. 

Another addition is the ProGuard. ProGuard obfuscates the program code and makes it hard to decompile and search for vulnerabilities.

# Short Demo

This video shows the registration process and a simple example conversation:

https://youtu.be/Uw9AwGSSw7E

# Code Details

All security methods and algorithms are contained in the [Cryptography.java](https://github.com/SemihBogaz/rhino/blob/master/app/src/main/java/com/infosec/rhino/Security/Cryptography.java) file and the class with the same name. This class implements a singleton pattern, and provides the required methods to read or create a new instance. For the algorithm implementations (for AES and RSA, and for key generation) Java's [Security package](https://docs.oracle.com/javase/7/docs/api/java/security/package-summary.html) is used. For storing keys simple IO operations with Android's internal storage is utilized. 


##Activity Explanation

Initial Activity => Checks if user is logged with his/herphone before. If so, redirects user to UserMainActivity if not redirects MainActivity for sms verification <br/>
MainActivity => Takes user phone number and sends sms verification code using Firebase sms verification method. Then rediretcs user to ProfileActivity <br/>
ProfileActivity => User enters a username if saves user will be redirected to UserMainActivity and a user will be created according to User class <br/>
UserMainActivity => User can tap any of the user and starts a chat. Tapping redirects user to ChatActivity <br/>
ChatActivity => User can send and receive messages in a secure way.
