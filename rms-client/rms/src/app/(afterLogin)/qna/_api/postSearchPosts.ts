import {Query} from "@/model/Query";

export async function postSearchPosts(search: Query, category: string) {
    return await fetch(`/w-api/post-service/post/search?category=${category}`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}