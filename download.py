import os

os.system("curl --request GET " + 
"--url 'https://api.tikapi.io/comment/list?media_id=7042543887659027759&count=30&cursor=30' " + 
"--header 'X-ACCOUNT-KEY: D4hAHGSHyHIaUclIOpSy8YJY4SO1uEqX' " +
"--header 'X-API-KEY: JAfnjxNTdoMjMn57h8imBgsopFG6nBsG' " +
"--header 'accept: application/json'")
