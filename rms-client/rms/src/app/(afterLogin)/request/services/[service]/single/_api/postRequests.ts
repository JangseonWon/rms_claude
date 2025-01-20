import {Query} from "@/model/Query";

export async function postRequests(query: Query) {
    return await fetch(`/w-api/catalog-service/requests/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(query),
        credentials: 'include',
        cache: 'no-store'
    });
}