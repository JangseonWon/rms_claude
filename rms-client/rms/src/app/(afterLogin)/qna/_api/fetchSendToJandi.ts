import {Post} from "@/model/Post";
import {PostComment} from "@/model/PostComment";

export async function fetchSendToJandi(userName: string, postId: number, category: string, post: Post, comment?: PostComment) {
    const res = await fetch(`/w-api/post-service/post/${postId}/message/${category}`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            user_name: userName,
            post: post,
            comment: comment
        }),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}