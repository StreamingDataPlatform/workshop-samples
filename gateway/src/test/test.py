from requests import Session
import json

def main():
    session = Session()
    req_param = '{"ID": "DELLEMC"}'
    responses = [session.post('http://0.0.0.0:3000/data', json=json.loads(req_param))]
    print(responses)

if __name__ == '__main__':
    main()