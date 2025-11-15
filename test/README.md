# Test

Testing a RESTful API needs Live testing for stable and mature verification that the software behaves as expected.

## E2E

End-to-end test for our RESTful API is a live testing suite to an already running instance in a staging, QA, pre-prod or
even in the prod environment.

We will use Venom https://github.com/ovh/venom for this purpose.

Alternatively, we could use Postman Newman and run collections https://github.com/postmanlabs/newman

If there would be a frontend also involved, it would be a good idea to have a separate suite for that.
With a browser automation tool we can automate the browser and test the full journey of the user.

- Selenium https://www.selenium.dev/
- Selenide(Java) https://selenide.org/
- Playwright https://playwright.dev/

## Load

Load testing is a way to measure the performance of a system under load. Will it break during a traffic spike?

- Gatling https://gatling.io/
- Hey - https://github.com/rakyll/hey
- Oha - https://github.com/hatoo/oha
- k6 - https://k6.io/

## Penetration

Find weaknesses in the system and how they can be exploited.

- How much damage can a malicious actor do to the system?
- How much technical information about the system can we get as a client that can be exploited?
- GDPR?
- Burp Suite - https://portswigger.net/burp 