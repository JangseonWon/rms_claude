import {Query} from "@/model/Query";

export async function putAlisUsers(search: Query) {
    return await fetch(`/w-api/management-service/alis/users`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}