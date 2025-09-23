/* eslint-env node */
import * as core from '@actions/core';
import * as github from '@actions/github';

async function run() {
  try {
    const token = globalThis.process?.env.GITHUB_TOKEN;
    if (!token) {
      throw new Error('❌ GITHUB_TOKEN is not set');
    }

    const octokit = github.getOctokit(token);

    const { owner, repo } = github.context.repo;
    const prNumber = github.context.payload.pull_request?.number;

    if (!prNumber) {
      return;
    }

    const { data: previousComments } = await octokit.rest.issues.listComments({
      owner,
      repo,
      issue_number: prNumber,
    });

    const previousLhciComment = previousComments.find((comment) =>
      comment.body.startsWith(`### Lighthouse report ✨\n`)
    );

    const newComment = globalThis.process?.env.LHCI_COMMENTS;
    if (!newComment) {
      throw new Error('❌ LHCI_COMMENTS not found in env');
    }

    if (previousLhciComment) {
      await octokit.rest.issues.updateComment({
        owner,
        repo,
        comment_id: previousLhciComment.id,
        body: newComment,
      });
    } else {
      await octokit.rest.issues.createComment({
        owner,
        repo,
        issue_number: prNumber,
        body: newComment,
      });
    }
  } catch (err) {
    core.setFailed(err.message);
  }
}

run();
