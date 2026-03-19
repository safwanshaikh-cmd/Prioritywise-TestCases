# Registration Test Cases

| Test Case ID | Test Scenario | Preconditions | Expected Result | Actual Result |
| --- | --- | --- | --- | --- |
| REG_001 | Open Registration Screen | User is on login page | Registration screen opens after clicking `Register` | Pending execution |
| REG_002 | Email Mandatory | User is on registration page | `Email is required.` warning is displayed when email is blank | Pending execution |
| REG_003 | Terms Validation | User is on registration page and has filled mandatory fields except terms checkbox | `You must accept the terms and conditions.` warning is displayed | Pending execution |
| REG_004 | Duplicate Email Registration | Existing registered email is available in test data | `The email address is already taken.` warning is displayed | Pending execution |
| REG_005 | Successful Registration | Unique email is available in test data and all fields are valid | Registration completes and success feedback is displayed | Pending execution |
