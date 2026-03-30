$pair = "admin:FkTm_Mgr_7nP0qT3uW6xZ9aC2dF5gI8k"
$base64 = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes($pair))
$headers = @{ Authorization = "Basic $base64" }

# Use .NET WebClient for larger uploads (no premature close)
$wc = New-Object System.Net.WebClient
$wc.Headers.Add("Authorization", "Basic $base64")
try {
    $response = $wc.UploadFile("http://10.10.1.26:8080/manager/text/deploy?path=/&update=true", "PUT", "C:\faktumEui\backend\target\faktum-backend-1.0.0-SNAPSHOT.war")
    [System.Text.Encoding]::UTF8.GetString($response)
} catch {
    Write-Output "ERROR: $($_.Exception.Message)"
    if ($_.Exception.InnerException) { Write-Output "INNER: $($_.Exception.InnerException.Message)" }
}
