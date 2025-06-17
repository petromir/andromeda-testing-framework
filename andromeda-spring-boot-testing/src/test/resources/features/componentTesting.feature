Feature: Test

  Background:

    Given [DB] table products has rows
      | name          | description            | price |
      | Football ball | Original Football ball | 35.6  |

  Scenario: Book created successfully
    Given [HTTP] client adds header X-Tenant = something
    Given http client sets JSON body
    """json
    {
      "ISBN": "978-6-0922-5527-6",
      "name": "War and Peace",
      "author": "Leo Tolstoy"
    }
    """

    When http client sends the request

    Then http client response status code must be 201
    Then http client response body must be empty

    Then database table bomb_jackpot must contain records
      | id | current_value | hidden_value | state   |
      | 1  | 17000.50      | 0.00         | CREATED |