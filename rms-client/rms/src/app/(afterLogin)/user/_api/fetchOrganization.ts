import {Query} from "@/model/Query";

export async function fetchOrganization(search: Query) {
    return await fetch(`/w-api/organization-service/organizations`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}