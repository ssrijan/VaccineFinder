Its a SpringBoot Application, one can run locally and search for the vaccine centers available in their locality.
Currently the project scans only for BANGALORE, with an hardcoded district value

https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=294 [the district_id can be changed]

Once the code is cloned, do run the Application class which will bootstrap the server and initiate the search [scheduled every 45seconds]
Incase of any match found, email notification would be sent to the configured email address.

CowinVaccineFinder has details for the email sender along with pwd.
