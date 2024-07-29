import {PostComment} from "@/model/PostComment";

export async function fetchComment(comment: PostComment) {
    return await fetch(`/w-api/post-service/post/comment`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(comment),
        credentials: 'include',
        cache: 'no-store'
    });
}