import { subscribe } from '../pubsub';
import { Message } from '../pubsub';
import {NatsConfig} from "../pubsub/types";
import {createAppJwt} from "../pubsub/userJwt";

const natsWsUrl = "wss://127.0.0.1";
const accessToken = 'EXAMPLE_ACCESS_TOKEN';
const exampleSubscribeSubject = "publisher.example.subject";

var config: NatsConfig;

async function printData(message: Message) {
    console.log('Received message:', message.data);
}

const onMessages = async (messages: Message[]) => {
    messages
        .filter((message) => message.subject === exampleSubscribeSubject)
        .forEach((message) => printData(message));
};

const onError = (text: string, error: Error) => {
    console.error(text, error);
};

async function main() {
    config = { url: natsWsUrl }
    const { userSeed: seed, jwt } = createAppJwt(accessToken);

    await subscribe({
        onMessages,
        onError,
        jwt: jwt,
        nkey: seed,
        config: config,
        subject: exampleSubscribeSubject
    });
    console.log('Connected to NATS server.');
}

main();