import {Query} from "@/model/Query";

export async function getExtensions(query: Query) {
    return await fetch(`/w-api/management-service/extensions`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(query),
        credentials: 'include',
        cache: 'no-store'
    });
}