import {Organization} from "@/model/Organization";

export async function PutOrganization(organization: Organization) {
    return await fetch(`/w-api/organization-service/organizations`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(organization),
        credentials: 'include',
        cache: 'no-store'
    });
}