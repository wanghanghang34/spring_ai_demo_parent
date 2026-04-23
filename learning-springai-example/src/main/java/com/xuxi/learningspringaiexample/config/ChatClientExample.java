package com.xuxi.learningspringaiexample.config;

//@Configuration
public class ChatClientExample {

//   @Bean
//    CommandLineRunner cli(
//            @Qualifier("openAiChatClient") ChatClient openAiChatClient,
//            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
//
//        return args -> {
//            var scanner = new Scanner(System.in);
//            ChatClient chat;
//
//            // Model selection
//            System.out.println("\nSelect your AI model:");
//            System.out.println("1. OpenAI");
//            System.out.println("2. Ollama");
//            System.out.print("Enter your choice (1 or 2): ");
//
//            String choice = scanner.nextLine().trim();
//
//            if (choice.equals("1")) {
//                chat = openAiChatClient;
//                System.out.println("Using OpenAI model");
//            } else {
//                chat = ollamaChatClient;
//                System.out.println("Using Ollama model");
//            }
//
//            // Use the selected chat client
//            System.out.print("\nEnter your question: ");
//            String input = scanner.nextLine();
//            String response = chat.prompt(input).call().content();
//            System.out.println("ASSISTANT: " + response);
//
//            scanner.close();
//        };
//    }
}
