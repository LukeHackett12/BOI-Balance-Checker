# BOI-Balance-Checker

[![Build Status](https://travis-ci.com/LukeHackett12/BOI-Balance-Checker.svg?token=wPNE4LQEjyzr5TDq7ygc&branch=master)](https://travis-ci.com/LukeHackett12/BOI-Balance-Checker)

## What is this project?

Good question! In essence, its me being extremely lazy. The Bank Of Ireland App doesn't store you're information so you have to repeatedly remember you're account number, pin code, and Date of Birth. Well no more! "Why not just remember basic information?" you may ask...

## How it works?

Bank Of Ireland has a 2-stage login process through their website, a clever twist being that it sometimes asks you for different information such as phone number vs DOB and different digits of your pin. The app saves all your information after the initial setup and will expertly navigate this deeply complex web form. Once it's [infiltrated the mainframe](https://media.giphy.com/media/xebOoxouppcGs/giphy.gif) it parses the resulting web page for you're account balances and displays them.


## Is my account information stored securely?

![alt text](https://media.giphy.com/media/VbOmzi6vtIPWo/giphy.gif)

Probably.... The app uses Android KeyStore to encrypt your account information and PIN so that it shouldn't be possible to read the information from outside the app, and the app will be secured with a six digit pin. BUT it is up to you whether you trust a relatively novice developer on this. I can say that the account information is not sent anywhere but the Bank Of Ireland website and those communications are secured, feel free to prove otherwise and let me know!
