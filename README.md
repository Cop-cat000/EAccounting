# EAccounting

## Description

EAccounting is a robust accounting software aimed at helping users manage their personal finances efficiently. This application allows users to track their income, expenses, accounts, and transactions in a user-friendly environment. The project is designed with simplicity in mind, while offering all the essential functionalities needed for financial management, such as creating accounts, tracking transactions.

### Key Features:
- Multi-account management
- Transaction tracking (expenses, income, transfers)
- Account balance and credit card limit monitoring
- Simple and clean user interface
- Secure login and user authentication

## Technologies Used

- Java: Core programming language for backend logic.
- MariaDB: Relational database used to store account and transaction data.
- JDBC: Java Database Connectivity API for managing database connections.
- Git: For version control and collaboration.
- Maven: For building automation.
- [Java telegram bot API](https://github.com/rubenlagus/TelegramBots)

## Database Schema

You can get acquainted with database schema with the pinned picture

The database includes the following tables:
- users: Stores user information.
- accounts: Holds information about user accounts, including account name, available balance, credit card limit, and a description field.
- transactions: Keeps track of all transactions related to user accounts, including amounts, dates, and comments.

## Usage

- Start the [telegram bot](https://t.me/E_Accounting_bot)
- Add multiple financial accounts (e.g., bank accounts, credit cards).
- Record transactions such as expenses, income, or transfers between accounts.
- Monitor your available balances and track credit card limits.

## Installation

1. Clone the repository:

   git clone https://github.com/Cop-cat000/EAccounting.git

2. Navigate to the project directory:

   cd EAccounting

3. Set up the MariaDB database:
    - Ensure MariaDB is installed and running.
    - Create a new database for the project and update the database configuration in the application.

4. Build the project using Maven:

   mvn package

5. Run the application:

   java -jar target/EAccounting-v2.0-jar-with-dependencies.jar [DB url] [DB username] [DB passwd] [Bot token]

## Contribution

Contributions are welcome! If you'd like to contribute, please fork the repository and create a pull request with a detailed explanation of your changes.

## License

MIT License

Copyright (c) [2024] [Sattarov Ruslan]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
---

Feel free to modify this template based on any additional features or details specific to your project.