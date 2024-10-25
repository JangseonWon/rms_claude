import {Query} from "@/model/Query";

export async function searchRequests(search: Query) {
    return await fetch(`/w-api/cart-service/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}