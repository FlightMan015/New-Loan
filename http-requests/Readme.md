# HTTP requests for manual API exploration & testing

The files in this folder are meant to execute API request against our own API and
also partner APIs. More info: https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html

## Authentication

Credentials are left blank intentionally inside `http-client.env.json`, because they MUST NOT
be stored under version control.
 
Instead, create a file `http-client.private.env.json` inside this directory and fill in the 
secrets there. This file is covered by `.gitignore`.

```
{
  "consors": {
    "clientid": "our-clientid",
    "username": "our-username",
    "password": "our-password"
  }
}
```

The values can be found in 1password.