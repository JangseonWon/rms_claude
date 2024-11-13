import {Query} from "@/model/Query";

export async function postExtensions(query: Query) {
    return await fetch(`/w-api/management-service/extensions/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(query),
        credentials: 'include',
        cache: 'no-store'
    });
}