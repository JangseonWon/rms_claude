import {Paging} from "@/model/Paging";

export async function getUsers(search: Paging) {
    return await fetch(`/w-api/management-service/users`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}