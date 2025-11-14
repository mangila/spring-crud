# alias httpc is my local systems curl binary with http2 features
# curl is reserved by Windows
$header = httpc -i --http2 http://localhost:8080/api/v1/employees

Write-Host $header