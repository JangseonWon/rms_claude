import {Query} from "@/model/Query";

export async function getServices(search: Query) {
    return await fetch(`/w-api/management-service/services`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}