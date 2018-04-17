package org.trustnote.wallet.network.hubapi;

public class HubRequest extends HubPackageBase {


    //Sample: ["request",{"command":"heartbeat","tag":"M1Ex7qjRIkKD9rXdjstq7wmgtLUu5wdW3ua//VV3hCk="}] to wss://shawtest.trustnote.org
    //Ref algorithm: ws.sent_echo_string = crypto.randomBytes(30).toString("base64");
    static String reqHeartBeat() {
        return "[\"request\",{\"command\":\"heartbeat\",\"tag\":\"M1Ex7qjRIkKD9rXdjstq7wmgtLUu5wdW3ua//VV3hCk=\"}]";
        //return "[\"request\",{\"command\":\"get_witnesses\",\"tag\":\"K6HNWLUpF+HFvqioh2A9zV/31R47OCNWjB79APQNU5c=\"}]";
    }

    public static String reqGetHistory() {
        return "[\"request\",{\"command\":\"light/get_history\",\"params\":{\"addresses\":[\"GI6TXYXRSB4JJZJLECF3F5DOTUZ5V7MX\",\"FCNRHWFKAQAAM25A2UGV3UEH3PEVJG4X\",\"66H5PZCTR25YXPLOSYFZ655EUF452M5B\",\"D44UEWC6YJ2VGIXA6OBF4XMB7FAFYOBB\",\"CF6X2AUBA5RBFR6EIICGACCSTVQQCWLF\",\"LRVHKENMSVDHJRCOWVKQFI2XON7NA7OD\",\"E5CF25T7GHPISHGHUTNPYXNCK72JFN7P\",\"VP5FT3XUQRVOORSTML7QV5DJVRUWVXEI\",\"O4NE4PKK67JIZT5OV54R6QUGF5PBKRID\",\"QT6IGTGPZNTLO6N53X7SYAS4LAZO3P3A\",\"NT4LDQ7WWHCYON7F64W3XBCCBZIEPEHQ\",\"P2IVYW7LPYLOIGPQKFYTVPDTAJD57ZVU\",\"BUQYM56FWGKZFYFGZA7T2WCPXSCZPUTA\",\"PMSOLG7PK7E7I55OJL7QGMQPQXZL65UB\",\"Y5JVBJ36Y63JGWXE5IG425I7PP4PUGO2\",\"ICKMYBTJZMJJGRGFYK4AN4ISUPI5HG7V\",\"BBRXQW2H5WMOP5THEMXMTNKLXAW42236\",\"67WBB4IFAVN4F7OF4QKTYVWGKUKXBYYI\",\"32CBQ7VWV6IA76EVG24BQZB633HJ4CV6\",\"DFNRPORXSFVXHEJGGIGW4PUKNKMQ34NW\"],\"witnesses\":[\"34NRY6HRBMWYMJQUKBF22R7JEKXYUHHW\",\"3C3OHD7WEFKV6RDF2U4M74RVK7YMDP7I\",\"4QBVMWX7DRAIVV4CZEVKS3IAQAFDPFBB\",\"4VCBX74SQMW46OKDTHXDVIFVIP2V6NFX\",\"4VYYR2YO6NV4NTF572AUBEKJLSTM4J4E\",\"AKB7DYDKTIMSOUNHUFB5PHKXOOYCM3YF\",\"B4Z366GZMCWJGPCQI5ROPK3L5OEBT7QD\",\"D27P6DGHLPO5A7MSOZABHOOWQ3BJ56ZI\",\"I6IK6MIYY34C4LV3JU6MNMGCJJN6VSKC\",\"KPQ3CRPBG5FSKVEH6Y76ETGD5D2N7QZ7\",\"NKLP6XURGMNT3ZUCJBCUVHB6BRNZTZL5\",\"QSOMNL7YPFQCYDKFUO63Y7RBLXDRDVJX\"]},\"tag\":\"bOo0Eeq5jWT8D0fwStljdp6T8JDIqaaKWEpzhQUgOvc=\"}]";
    }

    static String reqVersion() {
        return "[\"justsaying\",{\"mSubject\":\"version\",\"body\":{\"protocol_version\":\"1.0\",\"alt\":\"1\",\"library\":\"trustnote-common\",\"library_version\":\"0.1.0\",\"program\":\"TTT\",\"program_version\":\"1.1.0.5\"}}]";
    }

    static String reqLogin() {
        return "[\"justsaying\",{\"mSubject\":\"version\",\"body\":{\"protocol_version\":\"1.0\",\"alt\":\"1\",\"library\":\"trustnote-common\",\"library_version\":\"0.1.0\",\"program\":\"TTT\",\"program_version\":\"1.1.0.5\"}}]";
    }

    //TODO:
    public static byte[] reqChallenge(String challenge) {
        return "".getBytes();
    }
}
