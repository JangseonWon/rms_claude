import {Query} from "@/model/Query";

export async function postRequests(search: Query) {
    return await fetch(`/w-api/order-service/requests/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}