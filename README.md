A mobile application for keeping passwords safe

20160807005 - RAVAN SADIGLI

Passwords are the most crucial thing on the internet. The problem is, most of us don't know what makes a good password and aren't able to remember hundreds of them.
This causes a lot of problems. And, Our app helps users to remember their passwords securely. Firstly, each time we log in to the app, we need to log in with an email address and password. So, the most crucial thing to store the password is hashing algorithm. There are different hashing algorithms that exist. For this project, I used one of the most secure hashing algorithms called Password-Based Key Derivation Function 2 with hash-based message authentication code Secure Hash Algorithm 1. Let's look in detail at our hashing algorithm.
Password-Based Key Derivation Function 2 are used to used to reduce vulnerabilities of brute-force attacks. Password-Based Key Derivation Function 2 is part of RSA Laboratories' Public-Key Cryptography Standards  series. Password-Based Key Derivation Function applies a pseudo-random function to the login password, along with a salt value, and repeats the process multiple times to generate a derived key that can be used as a cryptographic key in further operations. The added computational work makes password cracking much more difficult. This process is also known as key stretching. And, the recommended number of iterations is one hundred thousand.
And, adding a salt to the password reduces the ability of rainbow table attacks and means that multiple passwords should be tested one by one, not all at once. The US National Institute of Standards and Technology recommends a salt length of one hundred twenty eight bits. Our getSalt()  function also uses one hundred twenty eight bit length for salt


Video:

https://youtu.be/N3Yrh4VnvXo
