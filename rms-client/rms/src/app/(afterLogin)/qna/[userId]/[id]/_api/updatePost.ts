import {Post} from "@/model/Post";

export async function updatePost(postId: string, post: Post) {
    return await fetch(`/w-api/post-service/post/${postId}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(post),
        credentials: 'include',
        cache: 'no-store'
    });
}