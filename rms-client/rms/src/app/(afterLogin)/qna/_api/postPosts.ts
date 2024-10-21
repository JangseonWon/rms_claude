import {Query} from "@/model/Query";

export async function postPosts(search: Query) {
    return await fetch(`/w-api/post-service/posts`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}