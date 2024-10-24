import {Query} from "@/model/Query";

export async function postServiceByUser(query: Query) {
    return await fetch(`/w-api/catalog-service/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: query ? JSON.stringify(query) : undefined,
        credentials: 'include',
        cache: 'no-store'
    });
}