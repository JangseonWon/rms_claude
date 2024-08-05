import {Post} from "@/model/Post";

export async function fetchSendToJandi(userName: string, postId: string, category: string, post: Post) {
    return await fetch(`/w-api/post-service/post/${postId}/message/${category}`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            user_name: userName,
            post: post
        }),
        credentials: 'include',
        cache: 'no-store'
    });
}