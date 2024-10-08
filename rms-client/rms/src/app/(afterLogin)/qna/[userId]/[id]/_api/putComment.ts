import {Comment} from "@/model/Comment";

export async function putComment(postId: string, comment: Comment) {
    return await fetch(`/w-api/post-service/posts/${postId}/comments`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(comment),
        credentials: 'include',
        cache: 'no-store'
    });
}