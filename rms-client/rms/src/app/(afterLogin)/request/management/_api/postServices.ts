import {Query} from "@/model/Query";

export async function postServices(query: Query) {
    return await fetch(`/w-api/management-service/services/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(query),
        credentials: 'include',
        cache: 'no-store'
    });
}