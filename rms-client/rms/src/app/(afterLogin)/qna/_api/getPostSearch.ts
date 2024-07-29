import {Paging} from "@/model/Paging";

export async function getPostSearch(search: Paging) {
    return await fetch(`/w-api/post-service/post/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}