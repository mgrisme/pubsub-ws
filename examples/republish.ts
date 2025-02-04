import { subscribe, publish, natsConnect } from '../pubsub';
import { Message } from '../pubsub';
import {NatsConfig} from "../pubsub/types";

const natsWsUrl = "wss://127.0.0.1";
const subscriberUserCredsJWT = 'USER_JWT';
const subscriberUserCredsSeed = 'EXAMPLE_ACCESS_TOKEN';
const publisherUserCredsJWT = 'USER_JWT';
const publisherUserCredsSeed = 'EXAMPLE_ACCESS_TOKEN';
const exampleSubscribeSubject = 'synternet.example.subject';
const examplePublishSubject = 'publisher.example.subject';

var subscriberConfig: NatsConfig;
var publisherConfig: NatsConfig;

async function republishData(message: Message) {
    console.log('Received message on', exampleSubscribeSubject, message.data);
    publish(examplePublishSubject, message.data, publisherConfig);
    console.log('Published message on', examplePublishSubject, message.data);
}

const onMessages = async (messages: Message[]) => {
    messages
        .filter((message) => message.subject === exampleSubscribeSubject)
        .forEach((message) => republishData(message));
};

const onError = (text: string, error: Error) => {
    console.error(text, error);
};

async function main() {
    subscriberConfig = { url: natsWsUrl }
    await subscribe({
        onMessages,
        onError,
        jwt: subscriberUserCredsJWT,
        nkey: subscriberUserCredsSeed,
        config: subscriberConfig,
        subject: exampleSubscribeSubject
    });
    console.log('Subscriber connected to NATS server.');
    const publishConnection = await natsConnect({ url: natsWsUrl }, publisherUserCredsJWT, publisherUserCredsSeed);
    publisherConfig = { url: natsWsUrl, connection: publishConnection }
    console.log('Publisher connected to NATS server.');
}

main();