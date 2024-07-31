import {Post} from "@/model/Post";

export async function fetchPost(post: Post) {
    return await fetch(`/w-api/post-service/post`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(post),
        credentials: 'include',
        cache: 'no-store'
    });
}