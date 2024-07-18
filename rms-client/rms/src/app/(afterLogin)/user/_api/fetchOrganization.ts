import {Paging} from "@/model/Paging";

export async function fetchOrganization(search: Paging) {
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