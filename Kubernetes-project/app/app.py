from flask import Flask
import os

app = Flask(__name__)

@app.route("/")
def home():
    return "Kubernetes Real World Project - Application Running"

@app.route("/health")
def health():
    return "Healthy", 200

@app.route("/config")
def config():
    return f"Environment: {os.getenv('APP_ENV', 'not-configured')}"

@app.route("/secret")
def secret():
    if os.getenv("APP_SECRET"):
        return "Secret is configured"
    return "Secret is NOT configured"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)


    ############### Testing github webhook #################
