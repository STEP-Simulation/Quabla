import urllib.request

if __name__=="__main__":
    version = '2.13.3'
    url1 = 'https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/' + version + '/jackson-annotations-' + version + '.jar'
    url2 = 'https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/' + version + '/jackson-core-' + version + '.jar'
    url3 = 'https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/' + version + '/jackson-databind-' + version + '.jar'
    
    out1 = 'jackson-annotations-' + version + '.jar'
    out2 = 'jackson-core-' + version + '.jar'
    out3 = 'jackson-databind-' + version + '.jar'

    urllib.request.urlretrieve(url1, out1)
    urllib.request.urlretrieve(url2, out2)
    urllib.request.urlretrieve(url3, out3)