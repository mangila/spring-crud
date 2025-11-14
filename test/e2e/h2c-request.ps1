# alias httpc is my local systems curl binary with http2 features
# curl is reserved by Windows
$headers = httpc -I --http2 http://localhost:8080/api/v1/employees

Write-Host $headers