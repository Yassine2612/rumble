(:JIQS: ShouldCrash; ErrorCode="RBML0003"; :)
let $hashingTF := get-transformer("HashingTF")
return $hashingTF(
    (structured-json-file("./src/main/resources/queries/rumbleML/sample-ml-string-data.json")),
    {"inputCol": 123, "numFeatures": 2}
)

(: column does not exist :)
